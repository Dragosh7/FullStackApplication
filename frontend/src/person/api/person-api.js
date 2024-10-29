import { HOST } from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";

const endpoint = {
    person: '/person',
    login: '/person/login', 
};

function getPersons(callback) {
    let request = new Request(HOST.backend_api + endpoint.person, {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function getPersonById(params, callback){
    let request = new Request(HOST.backend_api + endpoint.person + "/id:" + params, {
       method: 'GET'
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function postPerson(user, callback){
    let request = new Request(HOST.backend_api + endpoint.person, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });
    console.log("URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

export const updatePerson = (name, user, callback) => {
    let request = new Request(HOST.backend_api + endpoint.person + `/${name}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user),
    });
    console.log("Update URL: " + request.url);
    RestApiClient.performRequest(request, callback);
};

export const deletePerson = (id, callback) => {
    let request = new Request(HOST.backend_api + endpoint.person + `/${id}`, {
        method: 'DELETE',
    });
    console.log("Delete URL: " + request.url);
    RestApiClient.performRequest(request, callback);
};

function login(user, callback) {
    let request = new Request(HOST.backend_api + endpoint.login, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });
    console.log("Login URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}


export {
    getPersons,
    getPersonById,
    postPerson,
    login 
};
