import api,{pageItems} from './api.js'
export const memberStatuses=['ACTIVE','BLOCKED','BANNED']
export async function searchMembers(searchType,keyword,page=1,size=10){return pageItems((await api.get('/admin/members',{params:{page,size,searchType:searchType==='all'?undefined:searchType,keyword}})).data)}
export async function getMember(id){const r=await api.get(`/admin/members/${id}`);return r.data?.data??r.data}
export async function updateMemberStatus(id,status){const r=await api.patch(`/admin/members/${id}/status`,{status});return r.data?.data??r.data}

