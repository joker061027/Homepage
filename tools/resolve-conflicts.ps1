$ErrorActionPreference = "Stop"

function Get-CommentStyle($ext) {
  switch ($ext.ToLower()) {
    ".java" { return @{ type="block"; open="/*"; close="*/" } }
    ".js"   { return @{ type="block"; open="/*"; close="*/" } }
    ".ts"   { return @{ type="block"; open="/*"; close="*/" } }
    ".css"  { return @{ type="block"; open="/*"; close="*/" } }
    ".vue"  { return @{ type="block"; open="<!--"; close="-->" } }
    ".html" { return @{ type="block"; open="<!--"; close="-->" } }
    ".md"   { return @{ type="block"; open="<!--"; close="-->" } }
    ".yml"  { return @{ type="line";  line="#" } }
    ".yaml" { return @{ type="line";  line="#" } }
    ".properties" { return @{ type="line"; line="#" } }
    ".sql"  { return @{ type="line";  line="--" } }
    default { return @{ type="block"; open="/*"; close="*/" } }
  }
}

# 找出有冲突标记的文件
$conflicted = git diff --name-only --diff-filter=U | Where-Object { $_ -ne "" }
if (-not $conflicted) {
  Write-Host "No conflicted files found."
  exit 0
}

foreach ($file in $conflicted) {
  Write-Host "Resolving $file ..."
  $content = Get-Content -Raw -LiteralPath $file -Encoding UTF8

  if ($content -notmatch '<<<<<<< ' -or $content -notmatch '=======') {
    Write-Host "  No conflict markers found, skipping."
    continue
  }

  $ext = [System.IO.Path]::GetExtension($file)
  $style = Get-CommentStyle $ext

  # 逐冲突块处理：保留 ours，追加注释包裹的 theirs
  $pattern = '(?ms)<<<<<<<\s*HEAD\s*(.*?)\s*=======\s*(.*?)\s*>>>>>>>[^\r\n]*'
  $new = [System.Text.RegularExpressions.Regex]::Replace($content, $pattern, {
      param($m)
      $ours = $m.Groups[1].Value
      $theirs = $m.Groups[2].Value
      if ($style.type -eq "block") {
        return "$ours`r`n${($style.open)} COLLEAGUE VERSION (origin/order) START`r`n$theirs`r`n${($style.close)} COLLEAGUE VERSION (origin/order) END"
      } else {
        $commented = ($theirs -split "`r?`n") | ForEach-Object { "${($style.line)} $_" } | Out-String
        return "$ours`r`n$commented"
      }
    })

  Set-Content -LiteralPath $file -Value $new -Encoding UTF8
}

Write-Host "Conflict auto-resolution done."



