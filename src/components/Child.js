// import { useParams } from "react";
import { Link } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
//import { remote} from 'remote-file-size';
//import { stat } from "react-native-fs";

const Child = ({ id, file, backup, username, onGoBack, token, fileOwner }) => {
    const [data, setData] = useState();
    const [size, setSize] = useState();
    const inputEl = useRef(null);

    // useEffect(() => {
    //     const ctx = inputEl.canvas.getContext("2d");

    //     var imageObj1 = new Image();
    //     imageObj1.src = `http://localhost:8080/api/fileDrop/download?file_path=${username}/${file}`;

    //     const src = `http://localhost:8080/api/fileDrop/download?file_path=${username}/${file}`;
    //     const options = {
    //         method: "GET",
    //         headers: {
    //             Authorization: token,
    //         },
    //     };
    //     var test;
    //     fetch(src, options)
    //         .then((res) => res.blob())
    //         .then((blob) => {
    //             imageObj1.src = URL.createObjectURL(blob);
    //         });

    //     imageObj1.onload = () => {
    //         ctx.drawImage(imageObj1, 0, 0);
    //     };
    // });

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
        console.log(fileOwner + lfile);
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
            {file.endsWith("txt") ? (
                data
            ) : (
                <img id="myimage"></img>
                //<canvas ref={inputEl} width={300} height={300}></canvas>
            )}
        </>
    );
};

export default Child;
