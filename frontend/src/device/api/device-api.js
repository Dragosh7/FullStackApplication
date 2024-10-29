import { HOST } from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";

const endpoint = {
    device: '/device',
    person: '/person',
};


function getDevicesByPersonName(personName, callback) {
    let request = new Request(HOST.device_backend_api + endpoint.device + endpoint.person + `/${personName}`, {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function getPersons(callback) {
    let request = new Request(HOST.device_backend_api + endpoint.person, {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function getDevices(callback) {
    let request = new Request(HOST.device_backend_api + endpoint.device, {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function getUnlinkedDevices(callback) {
    let request = new Request(HOST.device_backend_api + endpoint.device + "/unlink", {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function getDeviceById(params, callback) {
    let request = new Request(HOST.device_backend_api + endpoint.device + `/${params.id}`, {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function addDevice(device, callback) {
    let request = new Request(HOST.device_backend_api + endpoint.device, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device),
    });
    console.log("URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

export const updateDevice = (id, device, callback) => {
    let request = new Request(HOST.device_backend_api + endpoint.device + `/${id}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device),
    });
    console.log("Update URL: " + request.url);
    RestApiClient.performRequest(request, callback);
};

export const linkDevice = (id, personName, callback) => {
    console.log("in api a venit:", JSON.stringify(personName));
    let request = new Request(HOST.device_backend_api + endpoint.device + "/link" + `/${id}`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(personName),
    });
    console.log("Update URL: " + request.url);
    RestApiClient.performRequest(request, callback);
};

export const deleteDevice = (id, callback) => {
    let request = new Request(HOST.device_backend_api + endpoint.device + `/${id}`, {
        method: 'DELETE',
    });
    console.log("Delete URL: " + request.url);
    RestApiClient.performRequest(request, callback);
};

export const unlinkDevice = (id, callback) => {
    let request = new Request(HOST.device_backend_api + endpoint.device + '/unlink' + `/${id}`, {
        method: 'DELETE',
    });
    console.log("Delete URL: " + request.url);
    RestApiClient.performRequest(request, callback);
};

export {
    getDevices,
    getDeviceById,
    addDevice,
    getPersons,getDevicesByPersonName,
    getUnlinkedDevices,
};
