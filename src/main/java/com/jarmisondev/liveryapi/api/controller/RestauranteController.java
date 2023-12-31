package com.jarmisondev.liveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarmisondev.liveryapi.domain.exception.EntidadeEmUsoException;
import com.jarmisondev.liveryapi.domain.exception.EntidadeNaoEncontradaException;
import com.jarmisondev.liveryapi.domain.model.Restaurante;
import com.jarmisondev.liveryapi.domain.service.CadastroRestauranteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    @Autowired
    private CadastroRestauranteService cadastroRestaurante;

    @GetMapping
    public List<Restaurante> listar(){
        return cadastroRestaurante.todos();
    }

    @GetMapping("/{restauranteId}")
    public ResponseEntity<Restaurante> buscarPor(@PathVariable Long restauranteId) {
        Restaurante restaurante = cadastroRestaurante.buscarPor(restauranteId);
        if (restaurante != null){
            return ResponseEntity.ok(restaurante);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> adicionar(@RequestBody Restaurante restaurante) {
        try {
            restaurante = cadastroRestaurante.salvar(restaurante);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(cadastroRestaurante.salvar(restaurante));

        } catch (EntidadeNaoEncontradaException e){
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{restauranteId}")
    public ResponseEntity<?> atualizar(@RequestBody Restaurante restaurante, @PathVariable Long restauranteId) {
        try {
            Restaurante restauranteAtual = cadastroRestaurante.buscarPor(restauranteId);
            if (restauranteAtual != null){
                BeanUtils.copyProperties(restaurante,restauranteAtual,"id");
                restauranteAtual = cadastroRestaurante.salvar(restauranteAtual);

                return ResponseEntity.ok().body(restauranteAtual);
            }

            return ResponseEntity.notFound().build();

        } catch (EntidadeNaoEncontradaException e){
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{restauranteId}")
    public ResponseEntity<?> remover(@PathVariable Long restauranteId) {
        try {
            Restaurante restaurante = cadastroRestaurante.buscarPor(restauranteId);
            cadastroRestaurante.remover(restaurante);

            return ResponseEntity.noContent().build();

        } catch (EntidadeNaoEncontradaException exception) {
            return ResponseEntity.notFound().build();

        } catch (EntidadeEmUsoException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/{restauranteId}")
    public ResponseEntity<?> atualizarParcial(@PathVariable Long restauranteId, @RequestBody Map<String,Object> campos) {
        Restaurante restauranteAtual = cadastroRestaurante.buscarPor(restauranteId);

        if (restauranteAtual == null){
            return ResponseEntity.notFound().build();
        }

        merge(campos,restauranteAtual);

        return atualizar(restauranteAtual, restauranteId);
    }

    private void merge(Map<String, Object> dadosOrigem, Restaurante restauranteDestino) {
        ObjectMapper objectMapper = new ObjectMapper();
        Restaurante restauranteOrigem = objectMapper.convertValue(dadosOrigem, Restaurante.class);

        dadosOrigem.forEach((nomePropriedade, valorPropriedade) ->{
            Field field = ReflectionUtils.findField(Restaurante.class,nomePropriedade);
            field.setAccessible(true);

            Object valorPropriedadeField = ReflectionUtils.getField(field,restauranteOrigem);

            ReflectionUtils.setField(field,restauranteDestino,valorPropriedadeField);
        });
    }
}
