$body = '{"gubun":"2","id":"admin","pw":"admin123"}'
try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8080/api/member/login' -Method Post -Body $body -ContentType 'application/json'
    Write-Host "SUCCESS"
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "ERROR: $($_.Exception.Message)"
}
