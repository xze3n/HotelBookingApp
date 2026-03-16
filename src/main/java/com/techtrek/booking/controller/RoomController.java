package com.techtrek.booking.controller;

import com.techtrek.booking.dto.CreateRoomRequest;
import com.techtrek.booking.dto.RoomResponse;
import com.techtrek.booking.dto.UpdateRoomRequest;
import com.techtrek.booking.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Rooms", description = "Room management endpoints")
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // POST endpoint /rooms
    @Operation(summary = "Create a new room", description = "Creates a new room.")
    @ApiResponse(responseCode = "201", description = "Room successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "409", description = "Room number already exists.")
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request){
        RoomResponse createdRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    // GET endpoint /rooms
    @Operation(summary = "List all rooms", description = "Returns a list of all rooms.")
    @ApiResponse(responseCode = "200", description = "Rooms listed successfully.")
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms(){

        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    // GET endpoint /rooms/{id}
    @Operation(summary = "Get a room by id", description = "Returns the details of a room with the specified id.")
    @ApiResponse(responseCode = "200", description = "Room successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Room not found.")
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id){
        RoomResponse room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    // PUT endpoint /rooms/{id}
    @Operation(summary = "Update a room", description = "Updates an existing room.")
    @ApiResponse(responseCode = "201", description = "Room successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "404", description = "Room not found.")
    @ApiResponse(responseCode = "409", description = "Room number already exists.")
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateRoomRequest request){
        RoomResponse updatedRoom = roomService.updateRoom(id, request);
        return ResponseEntity.ok(updatedRoom);
    }


    // DELETE endpoint /rooms/{id}
    @Operation(summary = "Delete a room", description = "Deletes a room by id.")
    @ApiResponse(responseCode = "204", description = "Room successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Room not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id){
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }


}
