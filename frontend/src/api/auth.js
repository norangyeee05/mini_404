import api from './client'

export const signup = (body) => api.post('/auth/signup', body)
export const checkId = (params) => api.get('/auth/check-id', { params })
export const login = (body) => api.post('/auth/login', body)
export const logout = () => api.post('/auth/logout')
export const getMe = () => api.get('/members/me')
export const updateMe = (body) => api.put('/members/me', body)
export const withdrawMe = (body) => api.delete('/members/me', { data: body })
