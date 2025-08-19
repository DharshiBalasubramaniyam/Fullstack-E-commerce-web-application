# Root Account
resource "aws_eks_access_entry" "root" {
  cluster_name      = aws_eks_cluster.purely_cluster.name
  principal_arn     = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
  type              = "STANDARD"
}

resource "aws_eks_access_policy_association" "root_AmazonEKSClusterAdminPolicy" {
  cluster_name  = aws_eks_cluster.purely_cluster.name
  policy_arn    = var.AmazonEKSClusterAdminPolicyArn
  principal_arn = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"

  access_scope {
    type       = "cluster"
  }
}

# Local CLI
resource "aws_eks_access_entry" "local_cli" {
  cluster_name      = aws_eks_cluster.purely_cluster.name
  principal_arn     = data.aws_iam_user.local_cli_user.arn
  type              = "STANDARD"
}

resource "aws_eks_access_policy_association" "local_cli_AmazonEKSClusterAdminPolicy" {
  cluster_name  = aws_eks_cluster.purely_cluster.name
  policy_arn    = var.AmazonEKSClusterAdminPolicyArn
  principal_arn = data.aws_iam_user.local_cli_user.arn

  access_scope {
    type       = "cluster"
  }
}

# GitHub actions
resource "aws_eks_access_entry" "github_actions" {
  cluster_name      = aws_eks_cluster.purely_cluster.name
  principal_arn     = data.aws_iam_user.github_actions_user.arn
  type              = "STANDARD"
}

resource "aws_eks_access_policy_association" "github_actions_AmazonEKSClusterAdminPolicy" {
  cluster_name  = aws_eks_cluster.purely_cluster.name
  policy_arn    = var.AmazonEKSClusterAdminPolicyArn
  principal_arn = data.aws_iam_user.github_actions_user.arn

  access_scope {
    type       = "cluster"
  }
}
