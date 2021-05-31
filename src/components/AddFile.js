import { useState } from "react";
import axios from "axios";

const AddFile = ({ onAddFile, onAdd, token, outfile }) => {
    const [name, setName] = useState("");
    const [file, setFile] = useState();
    const [dir, setDir] = useState("");
    const onSubmit = async (e) => {
        e.preventDefault();

        await onAddFile({ name, file, dir });
        //onAddFileLocal({ name, file, dir });

        // const data = new FormData();
        // data.append("files", file.file);
        // data.append("dir", file.dir);

        // axios
        //     .post("http://localhost:8080/api/fileDrop", data, {
        //         headers: { Authorization: token },
        //     })
        //     .then((res) => {
        //         console.log(res.statusText);
        //     });

        setName(file.name);
        onAdd();
    };

    const onAddFileLocal = (file) => {
        const data = new FormData();
        data.append("files", file.file);
        data.append("dir", file.dir);

        axios
            .post("http://localhost:8080/api/fileDrop", data, {
                headers: { Authorization: token },
            })
            .then((res) => {
                //refresh();
                console.log(res.statusText);
            });
        //onGoBack();
    };

    return (
        <form className="add-form" onSubmit={onSubmit}>
            <div className="row">
                <div className="col-md-6">
                    <form method="post" action="#" id="#">
                        <div className="form-control">
                            <label className="unselectable">Directory</label>
                            <input
                                type="text"
                                placeholder="Enter directory name for the file"
                                // value={username}
                                onChange={(e) => setDir(e.target.value)}
                            />
                        </div>
                        <div className="form-group files">
                            <label>Upload Your File </label>
                            <input
                                type="file"
                                className="form-control"
                                multiple=""
                                onChange={(e) => setFile(e.target.files[0])}
                            />
                        </div>
                    </form>
                </div>
            </div>

            <input
                type="submit"
                value="Add file"
                className="btn btn-block"
                readOnly
            />
        </form>
    );
};

export default AddFile;
