import request from '../utils/request'

export function uploadFile(data) {
  return request.post('/common/upload', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
