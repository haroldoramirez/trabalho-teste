package com.haroldo.minhasfinancas.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioLoginDTO {

    private String email;
    private String senha;

}
