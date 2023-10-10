
resource "aws_security_group_rule" "my-rule" {
  type              = "ingress"
  from_port         = 0
  to_port           = 65535
  protocol          = "tcp"
  cidr_blocks       = [aws_vpc.example.cidr_block]
  ipv6_cidr_blocks  = [aws_vpc.example.ipv6_cidr_block]
  security_group_id = "sg-123456"
  description       = "Security Group Rule"
}

resource "aws_alb_listener" "my-alb-listener"{
    port     = "443"
    protocol = "HTTPS"
}

# resource "azurerm_managed_disk" "source" {
#     encryption_settings {
#         enabled = var.enableEncryption
#     }
# }
resource "azurerm_managed_disk" "source" {
  name                 = "acctestmd"
  location             = azurerm_resource_group.source.location
  resource_group_name  = azurerm_resource_group.source.name
  storage_account_type = "Standard_LRS"
  create_option        = "Empty"
  disk_size_gb         = "1"

  tags = {
    environment = "staging"
  }
}

resource "aws_api_gateway_domain_name" "valid_security_policy" {
    security_policy = "TLS_1_2"
}
