$ErrorActionPreference = 'Stop'
$ts = [int][double]::Parse((Get-Date -UFormat %s))
$auth = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('admin:admin123'))
$hJson = @{ Authorization = "Basic $auth"; 'Content-Type' = 'application/json' }
$hAuth = @{ Authorization = "Basic $auth" }
$report = @{}

$depName = "Dept-$ts"
$respCreateDep = Invoke-WebRequest -UseBasicParsing -Method Post -Uri 'http://localhost:8080/api/v1/departamentos' -Headers $hJson -Body ("{`"nombre`":`"$depName`"}")
$depObj = $respCreateDep.Content | ConvertFrom-Json
$report.createDepartamento = @{ status = $respCreateDep.StatusCode; body = $depObj }

$respGetDep = Invoke-WebRequest -UseBasicParsing -Method Get -Uri ("http://localhost:8080/api/v1/departamentos/{0}" -f $depObj.id) -Headers $hAuth
$report.getDepartamento = @{ status = $respGetDep.StatusCode; body = ($respGetDep.Content | ConvertFrom-Json) }

$newName = "Dept-$ts-UPD"
$respUpdDep = Invoke-WebRequest -UseBasicParsing -Method Put -Uri ("http://localhost:8080/api/v1/departamentos/{0}" -f $depObj.id) -Headers $hJson -Body ("{`"nombre`":`"$newName`"}")
$report.updateDepartamento = @{ status = $respUpdDep.StatusCode; body = ($respUpdDep.Content | ConvertFrom-Json) }

$dep2Name = "DeptRel-$ts"
$respCreateDep2 = Invoke-WebRequest -UseBasicParsing -Method Post -Uri 'http://localhost:8080/api/v1/departamentos' -Headers $hJson -Body ("{`"nombre`":`"$dep2Name`"}")
$dep2Obj = $respCreateDep2.Content | ConvertFrom-Json

$empBody = "{`"nombre`":`"Emp-$ts`",`"direccion`":`"Dir-$ts`",`"telefono`":`"1234567890`",`"departamentoId`":$($dep2Obj.id)}"
$respCreateEmp = Invoke-WebRequest -UseBasicParsing -Method Post -Uri 'http://localhost:8080/api/v1/empleados' -Headers $hJson -Body $empBody
$empObj = $respCreateEmp.Content | ConvertFrom-Json
$report.createEmpleadoAssigned = @{ status = $respCreateEmp.StatusCode; body = $empObj }

$respFilter = Invoke-WebRequest -UseBasicParsing -Method Get -Uri ("http://localhost:8080/api/v1/empleados?departamentoId={0}&page=0&size=20" -f $dep2Obj.id) -Headers $hAuth
$filterObj = $respFilter.Content | ConvertFrom-Json
$report.filterByDepartamento = @{ status = $respFilter.StatusCode; totalElements = $filterObj.totalElements }

$respByDep = Invoke-WebRequest -UseBasicParsing -Method Get -Uri ("http://localhost:8080/api/v1/departamentos/{0}/empleados?page=0&size=20" -f $dep2Obj.id) -Headers $hAuth
$byDepObj = $respByDep.Content | ConvertFrom-Json
$report.byDepartamentoEndpoint = @{ status = $respByDep.StatusCode; totalElements = $byDepObj.totalElements }

try {
  Invoke-WebRequest -UseBasicParsing -Method Delete -Uri ("http://localhost:8080/api/v1/departamentos/{0}" -f $dep2Obj.id) -Headers $hAuth | Out-Null
  $report.deleteWithEmployee = @{ status = 204; note = 'Unexpected success' }
} catch {
  $status = [int]$_.Exception.Response.StatusCode
  $bodyRaw = ''
  try { $reader = New-Object IO.StreamReader($_.Exception.Response.GetResponseStream()); $bodyRaw = $reader.ReadToEnd() } catch {}
  $report.deleteWithEmployee = @{ status = $status; body = $bodyRaw }
}

$respGetEmp = Invoke-WebRequest -UseBasicParsing -Method Get -Uri ("http://localhost:8080/api/v1/empleados/{0}" -f $empObj.clave) -Headers $hAuth
$getEmpObj = $respGetEmp.Content | ConvertFrom-Json
$empUpdBody = @{
  nombre = $getEmpObj.nombre
  direccion = $getEmpObj.direccion
  telefono = $getEmpObj.telefono
  version = $getEmpObj.version
  departamentoId = $null
} | ConvertTo-Json -Compress
$respUnassign = Invoke-WebRequest -UseBasicParsing -Method Put -Uri ("http://localhost:8080/api/v1/empleados/{0}" -f $empObj.clave) -Headers $hJson -Body $empUpdBody
$report.unassignEmpleado = @{ status = $respUnassign.StatusCode; body = ($respUnassign.Content | ConvertFrom-Json) }

$respDeleteOk = Invoke-WebRequest -UseBasicParsing -Method Delete -Uri ("http://localhost:8080/api/v1/departamentos/{0}" -f $dep2Obj.id) -Headers $hAuth
$report.deleteAfterUnassign = @{ status = $respDeleteOk.StatusCode }

$respDeleteDep1 = Invoke-WebRequest -UseBasicParsing -Method Delete -Uri ("http://localhost:8080/api/v1/departamentos/{0}" -f $depObj.id) -Headers $hAuth
$report.deleteDepartamento = @{ status = $respDeleteDep1.StatusCode }

$swaggerStatus = (Invoke-WebRequest -UseBasicParsing -Method Get -Uri 'http://localhost:8080/swagger-ui/index.html' -Headers $hAuth).StatusCode
$report.swaggerUi = @{ status = $swaggerStatus }

$report | ConvertTo-Json -Depth 8 | Out-File .\crud-evidence.json -Encoding utf8
Write-Output 'CRUD_VALIDATION_OK'
