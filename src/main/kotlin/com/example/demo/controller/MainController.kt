package com.example.demo.controller

import com.example.demo.Config
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Document(collection="todos")
data class Todo(@Id var id:Long=0, var title:String="", var completed:Boolean=false, var order:Int=-1){

    // Needed because of jackson
    // Can also add jackson-kotlin, but need to update mapper. 1 line vs 15 ...
    @Suppress("unused")
    constructor():this(0)

    @Suppress("unused")
    val url:String
        get()="${Config.root}/$id"
}


interface ReactiveTodoRepository: ReactiveCrudRepository<Todo, Long>

class EmptyException : Throwable()

@RestController
@CrossOrigin
class MainController(val todoRepository: ReactiveTodoRepository) {

    @ExceptionHandler(EmptyException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun err404(): Mono<Void> = Mono.empty<Void>()


    @GetMapping("/")
    fun listTodo(): Flux<Todo> = todoRepository.findAll()

    @PostMapping("/")
    fun createTodo(@RequestBody todo: Todo): Mono<Todo> {
        todo.id= System.currentTimeMillis()
        return todoRepository.save(todo)
    }

    @DeleteMapping("/")
    fun clean():Mono<Void> = todoRepository.deleteAll()
    

    @GetMapping("/{id}")
    fun getTodo(@PathVariable("id") id: Long):Mono<Todo> = todoRepository.findById(id).
            switchIfEmpty(Mono.error(EmptyException()))


    @DeleteMapping("/{id}")
    fun remove(@PathVariable("id") id: Long):Mono<Void> = todoRepository.deleteById(id)


    @PatchMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody todo: Mono<Todo>): Mono<Todo>
        = todoRepository.findById(id).
            switchIfEmpty(Mono.error(EmptyException())).
            and(todo).map {
            if (!it.t2.title.isEmpty()) it.t1.title=it.t2.title
            if (it.t2.completed) it.t1.completed=true
            if (it.t2.order>-1) it.t1.order=it.t2.order
            it.t1
        }.flatMap { todoRepository.save(it) }
}
