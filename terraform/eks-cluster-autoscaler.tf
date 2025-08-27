resource "aws_iam_policy" "EKSClusterAutoscalerIAMPolicy" {
  name        = "EKSClusterAutoscalerIAMPolicy"
  description = "A custom IAM policy for Cluster Autoscaler access"
  policy      = file("policies/EKSClusterAutoscalerIAMPolicy.json")
}

resource "aws_iam_role" "EKSClusterAutoscalerIAMRole" {
  name        = "EKSClusterAutoscalerIAMRole"
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
            "${replace(data.aws_eks_cluster.purely_cluster.identity[0].oidc[0].issuer, "https://", "")}:sub" = "system:serviceaccount:kube-system:cluster-autoscaler"
          }
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "EKSClusterAutoscalerAttachment" { 
  role        = aws_iam_role.EKSClusterAutoscalerIAMRole.name
  policy_arn = aws_iam_policy.EKSClusterAutoscalerIAMPolicy.arn
}

resource "kubernetes_service_account" "cluster-autoscaler" {
  metadata {
    name = "cluster-autoscaler"
    namespace = "kube-system"
    annotations = {
        "eks.amazonaws.com/role-arn" = aws_iam_role.EKSClusterAutoscalerIAMRole.arn
    }
  }
}

resource "helm_release" "cluster-autoscaler" {
  name       = "cluster-autoscaler"
  repository = "https://kubernetes.github.io/autoscaler"
  chart      = "cluster-autoscaler"
  namespace = "kube-system"
  version = "9.50.1" 

  set = [
    {
      name  = "autoDiscovery.clusterName"
      value = aws_eks_cluster.purely_cluster.name
    },
    {
      name  = "rbac.serviceAccount.create"
      value = "false"
    },
    {
      name  = "rbac.serviceAccount.name"
      value = "cluster-autoscaler"
    },
    {
      name  = "awsRegion"
      value = var.region
    }
  ]
}