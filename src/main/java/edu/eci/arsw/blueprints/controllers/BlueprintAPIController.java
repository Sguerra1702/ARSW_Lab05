/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.controllers;

import java.util.Set;

import edu.eci.arsw.model.Blueprint;
import edu.eci.arsw.persistence.BlueprintNotFoundException;
import edu.eci.arsw.persistence.BlueprintPersistenceException;
import edu.eci.arsw.persistence.impl.InMemoryBlueprintPersistence;
import edu.eci.arsw.services.BlueprintsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */

@RestController
@Component
@RequestMapping
public class BlueprintAPIController {

    InMemoryBlueprintPersistence ibpp = new InMemoryBlueprintPersistence();

    @Autowired
    private BlueprintsServices blueprintsServices;

    @RequestMapping("/blueprints")
    public ResponseEntity<Set<Blueprint>> getBlueprints() {
        Set<Blueprint> blueprints = blueprintsServices.getAllBlueprints();
        return ResponseEntity.ok(blueprints);
    }

    @RequestMapping("/blueprints/{author}")
    public ResponseEntity<?> getBlueprintsByAuthor(@PathVariable String author) throws BlueprintNotFoundException {
        Set<Blueprint> blueprints = blueprintsServices.getBlueprintsByAuthor(author);
        if (blueprints == null || blueprints.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron planos del autor:" + author);
        }
        return ResponseEntity.ok(blueprints);
    }

    @GetMapping("/blueprints/{author}/{bpname}")
    public ResponseEntity<?> getBlueprintByAuthorAndName(@PathVariable String author, @PathVariable String bpname) {
        try {
            Blueprint blueprint = blueprintsServices.getBlueprint(author, bpname);
            return ResponseEntity.ok(blueprint);
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el plano '" + bpname + "' del autor '" + author + "'");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PostMapping("/planos")
    public ResponseEntity<?> addNewBluePrint(@RequestBody Blueprint blueprint) {
        try {
            blueprintsServices.addNewBlueprint(blueprint);
            return ResponseEntity.status(HttpStatus.CREATED).body("Plano creado Exitosamente");
        } catch (BlueprintPersistenceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear el plano");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor" + e.getMessage());
        }

    }

}
