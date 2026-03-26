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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @ApiResponse(responseCode = "200", description = "Room successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "404", description = "Room not found.")
    @ApiResponse(responseCode = "409", description = "Room number already exists.")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id){
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    // GET endpoint /rooms/search
    @Operation(summary = "Search for available rooms based on filters", description = "Search available rooms by date range, filter by type and capacity")
    @ApiResponse(responseCode = "200", description = "Room successfully retrieved.")
    @ApiResponse(responseCode = "400", description = "Invalid date range or parameters.")
    @GetMapping("/search")
    public ResponseEntity <List <RoomResponse>> searchRooms(
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String capacity ){

        List<RoomResponse> availableRooms = roomService.searchAvailableRooms(startDate,endDate,type,capacity);
        return ResponseEntity.ok(availableRooms);
    }

}
