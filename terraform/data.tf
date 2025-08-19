data "aws_iam_user" "github_actions_user" {
    user_name = var.github_actions_user_name
}

data "aws_iam_user" "local_cli_user" {
    user_name = var.local_cli_user_name
}

data "aws_caller_identity" "current" {}

# eksctl utils associate-iam-oidc-provider --region=us-east-1 --cluster=purely-cluster --approve
# eksctl create iamserviceaccount --cluster=purely-cluster --namespace=kube-system --name=alb-controller --attach-policy-arn=arn:aws:iam::019847571531:policy/AWSLoadBalancerControllerIAMPolicy --override-existing-serviceaccounts --region us-east-1 --approve
# eksctl delete iamserviceaccount --region=us-east-1 --cluster=purely-cluster --namespace=kube-system --name=alb-controller

# helm repo add eks https://aws.github.io/eks-charts
# helm repo update eks
# aws eks update-kubeconfig --region us-east-1 --name purely-cluster
# helm upgrade aws-load-balancer-controller eks/aws-load-balancer-controller -n kube-system --set clusterName=purely-cluster --set serviceAccount.create=false --set serviceAccount.name=alb-controller --set region=us-east-1 --set vpcId=vpc-0f8a2b9118c1071ff --version 1.13.0
# kubectl rollout restart deploy aws-load-balancer-controller -n kube-system
# aws iam create-policy-version --policy-arn arn:aws:iam::019847571531:policy/AWSLoadBalancerControllerIAMPolicy --policy-document file://"C:\Users\dhars\OneDrive\Documents\SE - UOK\React js + Spring boot projects\purely\iam-policy.json" --set-as-default