import { api, responseItems } from '../../services/backendApi'
import { noticeFromApi } from '../../services/apiMappers'

export async function getNotices({ page = 1, size = 10, searchType, keyword } = {}) {
  const response = await api.notices.list({ page, size, ...(searchType ? { searchType } : {}), ...(keyword ? { keyword } : {}) })
  return { ...response, items: responseItems(response).map(noticeFromApi) }
}
export async function getNoticeById(id) { return noticeFromApi(await api.notices.detail(id)) }
export async function createNotice(fields) { return api.notices.create(fields) }
export async function updateNotice(id, fields) { return api.notices.update(id, fields) }
export async function deleteNotice(id) { return api.notices.remove(id) }
