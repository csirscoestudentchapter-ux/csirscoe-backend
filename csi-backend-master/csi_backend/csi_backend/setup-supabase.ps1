# Supabase Environment Setup Script
# Run this script after getting your Supabase credentials

Write-Host "Setting up Supabase environment variables..." -ForegroundColor Green

# Your actual Supabase project details
$env:DATABASE_HOST = "aws-1-ap-southeast-1.pooler.supabase.com"
$env:DATABASE_PORT = "5432"
$env:DATABASE_NAME = "postgres"
$env:DATABASE_USER = "postgres.qzmnsrankbpqnuaxoyhp"
$env:DATABASE_PASSWORD = "Mayur@7666"

# Optional: Email configuration
$env:EMAIL_USERNAME = "contactus.csirscoe@gmail.com"
$env:EMAIL_PASSWORD = "lyizdksbwyoidroq"

Write-Host "Environment variables set!" -ForegroundColor Green
Write-Host "DATABASE_HOST: $env:DATABASE_HOST"
Write-Host "DATABASE_PORT: $env:DATABASE_PORT"
Write-Host "DATABASE_NAME: $env:DATABASE_NAME"
Write-Host "DATABASE_USER: $env:DATABASE_USER"

Write-Host "`nNow you can run: mvn spring-boot:run" -ForegroundColor Yellow
