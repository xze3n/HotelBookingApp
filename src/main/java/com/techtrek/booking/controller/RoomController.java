package com.techtrek.booking.controller;

import com.techtrek.booking.dto.CreateRoomRequest;
import com.techtrek.booking.dto.RoomResponse;
import com.techtrek.booking.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Rooms", description = "Room management endpoints")
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // POST endpoint /rooms
    @Operation(summary = "Create a new room")
    @ApiResponse(responseCode = "201", description = "Room successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "409", description = "Room number already exists.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request){
        return roomService.createRoom(request);
    }

    // GET enpoint /rooms
    @Operation(summary = "List all rooms")
    @ApiResponse(responseCode = "200", description = "Rooms listed successfully.")
    @GetMapping
    public List<RoomResponse> getAllRooms(){
        return roomService.getAllRooms();
    }
}
