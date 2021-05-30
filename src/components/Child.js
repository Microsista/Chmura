import { Link } from "react-router-dom";
import { useState, useEffect, useRef } from "react";

const Child = ({ id, file, backup, username, onGoBack, token, fileOwner }) => {
    const [data, setData] = useState();
    const [size, setSize] = useState();
    const inputEl = useRef(null);

    if (!file.endsWith("txt")) {
        var oReq = new XMLHttpRequest();
        const words = fileOwner.split("/");
        var owner = words[0];
        oReq.open(
            "GET",
            `http://localhost:8080/api/fileDrop/download?file_path=${owner}/${file}`,
            true
        );
        oReq.setRequestHeader("Authorization", token);
        // use multiple setRequestHeader calls to set multiple values
        oReq.responseType = "arraybuffer";
        oReq.onload = function (oEvent) {
            var arrayBuffer = oReq.response; // Note: not oReq.responseText
            if (arrayBuffer) {
                var u8 = new Uint8Array(arrayBuffer);
                var b64encoded = btoa(String.fromCharCode.apply(null, u8));
                var mimetype = "image/png"; // or whatever your image mime type is
                document.getElementById("myimage").src =
                    "data:" + mimetype + ";base64," + b64encoded;
            }
        };
        oReq.send(null);
    }

    const requestOptions = {
        method: "GET",
        headers: {
            Authorization: token,
        },
    };

    const useComponentWillMount = (func) => {
        const willMount = useRef(true);
        if (willMount.current) {
            func();
        }
        useComponentDidMount(() => {
            willMount.current = false;
        });
    };

    const useComponentDidMount = (func) => useEffect(func, []);

    useComponentWillMount(() => {
        const words = file.split("/");
        var lfile;
        if (words.length > 1) lfile = words[1];
        else lfile = words[0];
        console.log(fileOwner + "/" + lfile);
        const rawResponse = fetch(
            `http://localhost:8080/api/fileDrop/download?file_path=${fileOwner}/${lfile}`,
            requestOptions
        )
            .then((response) => {
                setSize(response.headers.get("content-length"));
                return response.text();
            })
            .then((responseJson) => {
                setData(responseJson);
            })
            .catch((error) => {
                console.error(error);
            });
    });

    const onBack = () => {
        onGoBack();
    };

    return (
        <>
            <Link to="/" onClick={onBack}>
                Go Back
            </Link>
            <div>File path: {file}</div>
            <div>File size: {size / 1000} KB</div>
            {file.endsWith("txt") ? data : <img id="myimage"></img>}
        </>
    );
};

export default Child;
