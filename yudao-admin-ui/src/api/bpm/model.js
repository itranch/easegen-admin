import request from '@/utils/request'

export function page(query) {
  return request({
    url: '/bpm/model/page',
    method: 'get',
    params: query
  })
}

export function exportBpmnXml(query) {
  return request({
    url: '/bpm/model/exportBpmnXml',
    method: 'get',
    params: query
  })
}

export function modelUpdate(data) {
  return request({
    url: '/bpm/model/update',
    method: 'POST',
    data: data
  })
}

export function modelSave(data) {
  return request({
    url: '/bpm/model/create',
    method: 'POST',
    data: data
  })
}

export function modelDelete(data) {
  return request({
    url: '/bpm/model/delete?modelId='+ data.modelId,
    method: 'POST',
    data: data
  })
}

export function modelDeploy(data) {
  return request({
    url: '/bpm/model/deploy?modelId='+ data.modelId,
    method: 'POST',
    data: data
  })
}
