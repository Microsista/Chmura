// import { useParams } from "react";
import { Link } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
//import { remote} from 'remote-file-size';
//import { stat } from "react-native-fs";

const Child = ({ id, file, backup, username }) => {
    const [data, setData] = useState();
    const [size, setSize] = useState();

    const requestOptions = {
        method: "GET",
        credentials: "include",
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
        const rawResponse = fetch(
            `http://localhost:8080/api/fileDrop/download?file_path=${username}/${file}`,
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

    return (
        <>
            <Link to="/">Go Back</Link>
            <div>File path: {file}</div>
            <div>File size: {size / 1000} KB</div>
            {file.endsWith("txt") ? (
                data
            ) : (
                <img
                    src={`http://localhost:8080/api/fileDrop/download?file_path=${username}/${file}`}
                />
            )}
        </>
    );
};

export default Child;
