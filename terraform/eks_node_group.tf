resource "aws_eks_node_group" "purely_node_group" {
  cluster_name = aws_eks_cluster.purely_cluster.name
  node_group_name = "purely_node_group"
  node_role_arn = aws_iam_role.purely_node_group_role.arn
  
  subnet_ids = [
    aws_subnet.purely_private_subnet_1a.id, aws_subnet.purely_private_subnet_1b.id,
    aws_subnet.purely_public_subnet_1a.id, aws_subnet.purely_public_subnet_1b.id
  ]

  scaling_config {
    desired_size = 1
    max_size     = 2
    min_size     = 1
  }

  update_config {
    max_unavailable = 1
  }

  depends_on = [
    aws_iam_role_policy_attachment.purely_node_group_role-AmazonEKSWorkerNodePolicy,
    aws_iam_role_policy_attachment.purely_node_group_role-AmazonEKS_CNI_Policy,
    aws_iam_role_policy_attachment.purely_node_group_role-AmazonEC2ContainerRegistryReadOnly,
  ]
}

resource "aws_iam_role" "purely_node_group_role" {
  name = "purely_node_group_role"

  assume_role_policy = jsonencode({
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
    }]
    Version = "2012-10-17"
  })
}

resource "aws_iam_role_policy_attachment" "purely_node_group_role-AmazonEKSWorkerNodePolicy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
  role       = aws_iam_role.purely_node_group_role.name
}

resource "aws_iam_role_policy_attachment" "purely_node_group_role-AmazonEKS_CNI_Policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
  role       = aws_iam_role.purely_node_group_role.name
}

resource "aws_iam_role_policy_attachment" "purely_node_group_role-AmazonEC2ContainerRegistryReadOnly" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = aws_iam_role.purely_node_group_role.name
}