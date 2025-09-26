package com.csi_rscoe.csi_backend.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ContactMsg {

    private String name;
    private String email;
    private String msg;

    @Override
    public String toString() {
        return super.toString();
    }
}
