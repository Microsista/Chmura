package com.example.demo.fileDrop;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@Table
@Entity
@NoArgsConstructor
public class ImageLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NonNull
    public String filePath;
    @NonNull
    public String geoHeight;
    @NonNull
    public String geoWidth;

    public String getHeightAndWidth() {
        return geoHeight + " " + geoWidth;
    }
}
