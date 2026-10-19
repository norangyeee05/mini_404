import { api, responseItems } from '../../services/backendApi'
import { qnaFromApi } from '../../services/apiMappers'

export async function getQnas({ page = 1, size = 10, searchType, keyword, inquiryType, status, mine = false, admin = false } = {}) {
  const params = { page, size, ...(searchType ? { searchType } : {}), ...(keyword ? { keyword } : {}), ...(inquiryType ? { inquiryType } : {}), ...(status ? { status } : {}) }
  const response = await (admin ? api.qna.adminList(params) : mine ? api.qna.my(params) : api.qna.list(params))
  return { ...response, items: responseItems(response).map(qnaFromApi) }
}
export async function getQna(id, admin = false) { return qnaFromApi(await (admin ? api.qna.adminDetail(id) : api.qna.detail(id))) }
const qnaFieldsToApi = fields => ({ ...fields, isPrivate: fields.isPrivate ?? (fields.isPublic === false) })
export async function createQna(fields, files) { return api.qna.create(qnaFieldsToApi(fields), files) }
export async function updateQna(id, fields, files) { return api.qna.update(id, qnaFieldsToApi(fields), files) }
export async function deleteQna(id, admin = false) { return admin ? api.qna.adminRemove(id) : api.qna.remove(id) }
export async function answerQna(id, answer, update = false) { return api.qna.answer(id, answer, update) }
