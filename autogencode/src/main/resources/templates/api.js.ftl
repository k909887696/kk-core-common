import request from '@/utils/request4Quantization'
import config from '@/config'

export function get_${table.name}_page_list(params, isLoading) {
    return request({
        url: config.${package.ModuleName}_api_url + '<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/api/v1/${table.name}/get_${table.name}_page_list',
        method: 'post',
        data: params,
        isLoading
    })
}

export function delete(params, isLoading) {
    return request({
        url: config.${package.ModuleName}_api_url + '<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/api/v1/${table.name}/delete',
        method: 'post',
        data: params,
        isLoading
    })
}

export function insert(params, isLoading) {
    return request({
    url: config.quantization_api_url + '<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/api/v1/${table.name}/insert',
        method: 'post',
        data: params,
        isLoading
    })
}

export function update(params, isLoading) {
    return request({
        url: config.${package.ModuleName}_api_url + '<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/api/v1/${table.name}/update',
        method: 'post',
        data: params,
        isLoading
    })
}

export function get_details(params, isLoading) {
    return request({
        url: config.${package.ModuleName}_api_url + '<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/api/v1/${table.name}/get_details',
        method: 'post',
        data: params,
        isLoading
    })
}