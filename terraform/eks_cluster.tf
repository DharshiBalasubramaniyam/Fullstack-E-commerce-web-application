resource "aws_eks_cluster" "purely_cluster" {
  name = "purely-cluster"

  access_config {
    authentication_mode = "API"
  }

  role_arn = aws_iam_role.purely_cluster_role.arn
  version  = "1.32"

  vpc_config {
    subnet_ids = [
      aws_subnet.purely_public_subnet_1a.id,
      aws_subnet.purely_public_subnet_1b.id,
      aws_subnet.purely_private_subnet_1a.id,
      aws_subnet.purely_private_subnet_1b.id
    ]
  }

  depends_on = [
    aws_iam_role_policy_attachment.purely_cluster_AmazonEKSClusterPolicy,
  ]
}

resource "aws_iam_role" "purely_cluster_role" {
  name = "purely_cluster_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "sts:AssumeRole",
          "sts:TagSession"
        ]
        Effect = "Allow"
        Principal = {
          Service = "eks.amazonaws.com"
        }
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "purely_cluster_AmazonEKSClusterPolicy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.purely_cluster_role.name
}
