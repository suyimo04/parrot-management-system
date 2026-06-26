import request from '../utils/request'

export function getPublicParrots(params) {
  return request.get('/public/parrot/list', { params })
}

export function getPublicParrot(id) {
  return request.get(`/public/parrot/${id}`)
}

export function getPublicNotices(params) {
  return request.get('/public/notice/list', { params })
}

export function getPublicNotice(id) {
  return request.get(`/public/notice/${id}`)
}
