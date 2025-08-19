variable region {
	default = "us-east-1"
}

variable "vpc_cidr" {
	default = "10.0.0.0/16"
}

variable "github_actions_user_name" {
	default = "github-actions-eks-user"
}

variable "local_cli_user_name" {
	default = "dhar-cli"
}

variable "AmazonEKSClusterAdminPolicyArn" {
	default = "arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy"
}
