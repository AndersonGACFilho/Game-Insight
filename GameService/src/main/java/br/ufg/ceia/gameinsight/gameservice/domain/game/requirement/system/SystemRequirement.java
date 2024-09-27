package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system;

import br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.parts.*;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents the system requirements of a game.
 * The system requirements are the minimum and recommended hardware and software requirements for a game.
 */
@Entity
@Table(name = "system_requirement")
public class SystemRequirement implements Serializable{
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the system requirement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The operating system required for the game.
     */
    private OperationSystem operationSystem;

    /**
     * The CPU required for the game.
     */
    private CPU cpu;

    /**
     * The GPU required for the game.
     */
    private GPU gpu;

    /**
     * The RAM required for the game.
     */
    private RAM ram;

    /**
     * The storage required for the game.
     */
    private Storage storage;

    /**
     * The Api required for the game.
     * Like DirectX, OpenGL, Vulkan, etc.
     */
    private Api api;

    /**
     * The constructor of the class.
     */
    public SystemRequirement() {
    }

    /**
     * The constructor of the class.
     * @param id The unique identifier of the system requirement.
     * @param operationSystem The operating system required for the game.
     * @param cpu The CPU required for the game.
     * @param gpu The GPU required for the game.
     * @param ram The RAM required for the game.
     * @param storage The storage required for the game.
     * @param api The Api required for the game.
     */
    public SystemRequirement(Integer id, OperationSystem operationSystem, CPU cpu, GPU gpu, RAM ram, Storage storage, Api api) {
        this.id = id;
        this.operationSystem = operationSystem;
        this.cpu = cpu;
        this.gpu = gpu;
        this.ram = ram;
        this.storage = storage;
        this.api = api;
    }

    /**
     * Get the unique identifier of the system requirement.
     * @return The unique identifier of the system requirement.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Get the operating system required for the game.
     * @return The operating system required for the game.
     */
    public OperationSystem getOperationSystem() {
        return operationSystem;
    }

    /**
     * Get the CPU required for the game.
     * @return The CPU required for the game.
     */
    public CPU getCpu() {
        return cpu;
    }

    /**
     * Get the GPU required for the game.
     * @return The GPU required for the game.
     */
    public GPU getGpu() {
        return gpu;
    }

    /**
     * Get the RAM required for the game.
     * @return The RAM required for the game.
     */
    public RAM getRam() {
        return ram;
    }

    /**
     * Get the storage required for the game.
     * @return The storage required for the game.
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Get the Api required for the game.
     * @return The Api required for the game.
     */
    public Api getApi() {
        return api;
    }

    /**
     * Set the unique identifier of the system requirement.
     * @param id The unique identifier of the system requirement.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Set the operating system required for the game.
     * @param operationSystem The operating system required for the game.
     */
    public void setOperationSystem(OperationSystem operationSystem) {
        this.operationSystem = operationSystem;
    }

    /**
     * Set the CPU required for the game.
     * @param cpu The CPU required for the game.
     */
    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    /**
     * Set the GPU required for the game.
     * @param gpu The GPU required for the game.
     */
    public void setGpu(GPU gpu) {
        this.gpu = gpu;
    }

    /**
     * Set the RAM required for the game.
     * @param ram The RAM required for the game.
     */
    public void setRam(RAM ram) {
        this.ram = ram;
    }

    /**
     * Set the storage required for the game.
     * @param storage The storage required for the game.
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     * Set the Api required for the game.
     * @param api The Api required for the game.
     */
    public void setApi(Api api) {
        this.api = api;
    }

    @Override
    public String toString() {
        return "SystemRequirement{" +
                "id=" + id +
                ", operationSystem=" + operationSystem +
                ", cpu=" + cpu +
                ", gpu=" + gpu +
                ", ram=" + ram +
                ", storage=" + storage +
                ", api=" + api +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemRequirement)) return false;

        SystemRequirement that = (SystemRequirement) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(operationSystem, that.operationSystem))
            return false;
        if (!Objects.equals(cpu, that.cpu)) return false;
        if (!Objects.equals(gpu, that.gpu)) return false;
        if (!Objects.equals(ram, that.ram)) return false;
        if (!Objects.equals(storage, that.storage)) return false;
        return Objects.equals(api, that.api);
    }
}
