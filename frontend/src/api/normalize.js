export function pick(source, ...keys) {
  for (const key of keys) if (source?.[key] !== undefined && source?.[key] !== null) return source[key]
  return ''
}

export function normalizeNotice(item = {}) {
  return { ...item, id: pick(item, 'noticeId', 'id'), title: pick(item, 'title'), content: pick(item, 'content', 'body'), writer: pick(item, 'writer', 'authorName', 'createdBy'), views: pick(item, 'viewCount', 'views'), date: pick(item, 'createdAt', 'date'), viewCount: pick(item, 'viewCount', 'views'), createdAt: pick(item, 'createdAt', 'date') }
}

export function normalizeResource(item = {}) {
  const files = (item.files ?? (item.file ? [item.file] : [])).map((file) => ({
    ...file,
    fileId: pick(file, 'fileId', 'id'),
    fileName: pick(file, 'fileName', 'originalFileName'),
    fileSize: pick(file, 'fileSize', 'size'),
  }))
  const file = files[0] ?? {}
  return { ...item, id: pick(item, 'resourceId', 'id'), title: pick(item, 'title'), content: pick(item, 'content', 'description'), writer: pick(item, 'writer', 'authorName', 'createdBy'), views: pick(item, 'viewCount', 'views'), date: pick(item, 'createdAt', 'date'), files, fileName: pick(item, 'fileName', 'originalFileName') || file.fileName, fileId: pick(item, 'fileId') || file.fileId, fileSize: pick(item, 'fileSize', 'size') || file.fileSize }
}

export function normalizeQna(item = {}) {
  const type = pick(item, 'inquiryType', 'category', 'type')
  const category = ({ FACILITY: '시설', OPERATION: '운행', ETC: '기타' })[type] || type
  const status = ({ WAITING: '답변대기', ANSWERED: '답변완료' })[pick(item, 'status', 'answerStatus')] || pick(item, 'status', 'answerStatus')
  return { ...item, id: pick(item, 'qnaId', 'id'), title: pick(item, 'title'), content: pick(item, 'content', 'question'), writer: pick(item, 'writer', 'authorName', 'createdBy'), views: pick(item, 'viewCount', 'views'), date: pick(item, 'createdAt', 'date'), status, answer: pick(item, 'answer', 'answerContent'), category, inquiryType: type, visibility: pick(item, 'visibility', 'openType') }
}
