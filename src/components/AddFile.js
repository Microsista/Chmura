import { useState } from "react";

const AddFile = ({ onAddFile, onAdd }) => {
    const [name, setName] = useState("");
    const [file, setFile] = useState();
    const onSubmit = (e) => {
        e.preventDefault();

        onAddFile({ name, file });
        setName(file.name);
        onAdd();
    };

    return (
        <form className="add-form" onSubmit={onSubmit}>
            <div class="row">
                <div class="col-md-6">
                    <form method="post" action="#" id="#">
                        <div class="form-group files">
                            <label>Upload Your File </label>
                            <input
                                type="file"
                                class="form-control"
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
