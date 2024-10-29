// function performRequest(request, callback){
//     fetch(request)
//         .then(
//             function(response) {
//                 if (response.ok) {
//                     response.json().then(json => callback(json, response.status,null));
//                 }
//                 else {
//                     response.json().then(err => callback(null, response.status,  err));
//                 }
//             })
//         .catch(function (err) {
//             //catch any other unexpected error, and set custom code for error = 1
//             callback(null, 1, err)
//         });
// }

function performRequest(request, callback) {
    fetch(request)
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    // Handle error response as a string
                    return Promise.reject({ status: response.status, message: text });
                });
            }
            return response.text(); // Handle the string response
        })
        .then(data => {
            // Here, 'data' is a plain string
            callback(data, 200); // Call the callback with the string and status code
        })
        .catch(error => {
            console.error("Error:", error);
            callback(null, error.status || 500, error.message || "Unknown error");
        });
}

module.exports = {
    performRequest
};
