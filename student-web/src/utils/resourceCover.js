const palettes = [
  ['#2563EB', '#60A5FA', '#DBEAFE'],
  ['#16A34A', '#86EFAC', '#DCFCE7'],
  ['#F59E0B', '#FCD34D', '#FEF3C7'],
  ['#DC2626', '#FCA5A5', '#FEE2E2'],
  ['#7C3AED', '#C4B5FD', '#EDE9FE'],
  ['#0891B2', '#67E8F9', '#CFFAFE']
]

function hashText(text) {
  return Array.from(text || 'resource').reduce((sum, char) => sum + char.charCodeAt(0), 0)
}

function keywordOf(resource = {}) {
  const text = `${resource.title || ''} ${resource.typeName || ''}`.toLowerCase()
  if (text.includes('java')) return 'Java'
  if (text.includes('mysql') || text.includes('sql')) return 'MySQL'
  if (text.includes('vue')) return 'Vue'
  if (text.includes('考试') || text.includes('题')) return 'Exam'
  if (text.includes('数据')) return 'DB'
  return 'Study'
}

export function resourceCover(resource = {}) {
  if (resource.coverUrl) return resource.coverUrl
  if (resource.imageUrl) return resource.imageUrl
  const title = resource.title || '学习资料'
  const keyword = keywordOf(resource)
  const palette = palettes[hashText(title) % palettes.length]
  const svg = `
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 360">
  <defs>
    <linearGradient id="g" x1="0" x2="1" y1="0" y2="1">
      <stop offset="0" stop-color="${palette[0]}"/>
      <stop offset="1" stop-color="${palette[1]}"/>
    </linearGradient>
  </defs>
  <rect width="640" height="360" rx="28" fill="${palette[2]}"/>
  <circle cx="544" cy="80" r="112" fill="${palette[1]}" opacity=".32"/>
  <circle cx="86" cy="306" r="116" fill="${palette[0]}" opacity=".18"/>
  <rect x="62" y="58" width="246" height="176" rx="18" fill="url(#g)"/>
  <rect x="96" y="92" width="178" height="16" rx="8" fill="white" opacity=".78"/>
  <rect x="96" y="130" width="132" height="14" rx="7" fill="white" opacity=".62"/>
  <rect x="96" y="166" width="156" height="14" rx="7" fill="white" opacity=".62"/>
  <rect x="360" y="86" width="188" height="188" rx="28" fill="white" opacity=".86"/>
  <text x="454" y="178" text-anchor="middle" font-family="Arial, sans-serif" font-size="54" font-weight="700" fill="${palette[0]}">${keyword}</text>
  <text x="66" y="294" font-family="Microsoft YaHei, Arial, sans-serif" font-size="28" font-weight="700" fill="#111827">${title}</text>
</svg>`
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}
