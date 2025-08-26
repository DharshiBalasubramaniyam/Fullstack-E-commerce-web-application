resource "aws_iam_openid_connect_provider" "alb_openid_connect_provider" {
  url = data.aws_eks_cluster.purely_cluster.identity[0].oidc[0].issuer

  client_id_list = [
    "sts.amazonaws.com",
  ]

  thumbprint_list = ["9e99a48a9960b14926bb7f3b02e22da2b0ab7280"]
}

resource "aws_iam_policy" "AWSLoadBalancerControllerIAMPolicy" {
  name        = "AWSLoadBalancerControllerIAMPolicy"
  description = "A custom IAM policy for Application Load Balancer Controller access"
  policy      = file("AWSLoadBalancerControllerIAMPolicy.json")
}

resource "aws_iam_role" "AWSLoadBalancerControllerIAMRole" {
  name        = "AWSLoadBalancerControllerIAMRole"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = aws_iam_openid_connect_provider.alb_openid_connect_provider.arn
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringEquals = {
            "${replace(data.aws_eks_cluster.purely_cluster.identity[0].oidc[0].issuer, "https://", "")}:sub" = "system:serviceaccount:kube-system:alb-controller"
          }
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "AWSLoadBalancerControllerAttachment" { 
  role        = aws_iam_role.AWSLoadBalancerControllerIAMRole.name
  policy_arn = aws_iam_policy.AWSLoadBalancerControllerIAMPolicy.arn
}

resource "kubernetes_service_account" "alb-controller" {
  metadata {
    name = "alb-controller"
    namespace = "kube-system"
    annotations = {
        "eks.amazonaws.com/role-arn" = aws_iam_role.AWSLoadBalancerControllerIAMRole.arn
    }
  }
}

# The ALB Controller pod will use service account.
# A service account is a Kubernetes object that pods use for identity. This service account is annotated with the IAM role ARN. 
# An IAM role is a set of permissions that can be assumed by a trusted entity.
#   Trust Policy - The role will be assumed by the Kubernetes service account running the ALB Controller.
#   Permission Policy - It defines what the ALB Controller can do in your AWS account. 
resource "helm_release" "aws-load-balancer-controller" {
  name       = "aws-load-balancer-controller"
  repository = "https://aws.github.io/eks-charts"
  chart      = "aws-load-balancer-controller"
  namespace = "kube-system"
  version = "1.13.0"

  set = [
    {
      name  = "clusterName"
      value = aws_eks_cluster.purely_cluster.name
    },
    {
      name  = "serviceAccount.create"
      value = "false"
    },
    {
      name  = "serviceAccount.name"
      value = "alb-controller"
    },
    {
      name  = "region"
      value = var.region
    },
    {
      name  = "vpcId"
      value = aws_vpc.purely_vpc.id
    }
  ]
}
