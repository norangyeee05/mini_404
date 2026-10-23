import { api, responseItems } from '../../services/backendApi'
import { noticeFromApi } from '../../services/apiMappers'

export async function getNotices({ page = 1, size = 10, searchType, keyword, admin = false } = {}) {
  const params = { page, size, ...(searchType ? { searchType } : {}), ...(keyword ? { keyword } : {}) }
  const response = await (admin ? api.notices.adminList(params) : api.notices.list(params))
  return { ...response, items: responseItems(response).map(noticeFromApi) }
}
export async function getNoticeById(id, admin = false) { return noticeFromApi(await (admin ? api.notices.adminDetail(id) : api.notices.detail(id))) }
export async function createNotice(fields, files = []) { return api.notices.create(fields, files) }
export async function updateNotice(id, fields, files = []) { return api.notices.update(id, fields, files) }
export async function deleteNotice(id) { return api.notices.remove(id) }
