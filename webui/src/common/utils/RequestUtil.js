export const isDev = process.env.NODE_ENV === "development";

export const SERVER_URL = isDev ? "http://localhost:7865" : "";
export const PROXY_URL = isDev ? "http://localhost:7865/proxy" : "/proxy";

// Get the default headers of the request
const getHeaders = () => {
    return localStorage.getItem("token") ? {Authorization: "Basic " + localStorage.getItem("token")} : {};
}

export const proxyRequest = async (path, method = "GET", body = {}, headers = {}) => {
    return await fetch(PROXY_URL + "/" + path, {
        headers: {...getHeaders(), ...headers}, method,
        body: method !== "GET" ? new URLSearchParams(body) : undefined
    });
}

// Run a plain request with all default values
export const request = async (path, method = "GET", body = {}, headers = {}, abort = true) => {
    const controller = new AbortController();
    if (abort) setTimeout(() => {controller.abort()}, 10000);


    return await fetch("/api/" + path, {
        headers: {...getHeaders(), ...headers}, method,
        body: method !== "GET" ? new URLSearchParams(body) : undefined,
        signal: controller.signal
    });
}

// Run a GET request and get the json of the response
export const jsonRequest = async (path, headers = {}) => {
    return (await request(path, "GET", null, headers)).json();
}

export const jsonProxyRequest = async (path, headers = {}) => {
    return (await proxyRequest(path, "GET", null, headers)).json();
}

// Dispatches the provided command
export const dispatchCommand = (command) => {
    return postRequest("console", {command});
}

// Run a POST request and post some values
export const postRequest = async (path, body = {}, headers = {}) => {
    return await request(path, "POST", body, headers);
}

// Run a PUT request update a resource
export const putRequest = async (path, body = {}, headers = {}) => {
    return await request(path, "PUT", body, headers);
}

// Run a PATCH request update a resource
export const patchRequest = async (path, body = {}, headers = {}) => {
    return await request(path, "PATCH", body, headers);
}

// Run a DELETE request and delete a resource
export const deleteRequest = async (path, body = {}, headers = {}) => {
    return await request(path, "DELETE", body, headers);
}