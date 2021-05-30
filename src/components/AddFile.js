import { useState } from "react";

const AddFile = ({ onAddFile, onAdd }) => {
    const [name, setName] = useState("");
    const [file, setFile] = useState();
    const [dir, setDir] = useState("");
    const onSubmit = (e) => {
        e.preventDefault();

        onAddFile({ name, file, dir });
        setName(file.name);
        onAdd();
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
