package br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig;

import br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts.*;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * This class represents the User's PC Data into the User collection.
 * <p>
 * This class is used to represent the user's PC Configuration JSON object in
 * the MongoDB database.
 * <p>
 * This class includes the following attributes:
 * <ul>
*  <li>The OS of the User's PC </li>
 * <li>The CPU of the User's PC </li>
 * <li>The GPU of the User's PC </li>
 * <li>The RAM of the User's PC </li>
 * <li>The storage of the User's PC </li>
 * <li>The DirectX version of the User's PC </li>
 */
public class UserPc {
    /**
     * The OS of the User's PC.
     */
    @Field
    private OperationalSystem os;

    /**
     * The CPU of the User's PC.
     */
    @Field
    private CPU cpu;

    /**
     * The GPU of the User's PC.
     */
    @Field
    private GPU gpu;

    /**
     * The RAM of the User's PC.
     */
    @Field
    private RAM ram;

    /**
     * The storage of the User's PC.
     */
    @Field
    private Storage storage;

    /**
     * The DirectX version of the User's PC.
     */
    @Field
    private DirectX directX;

    /**
     * The default constructor.
     */
    public UserPc() {

    }

    /**
     * The constructor with all attributes.
     *
     * @param os The OS of the User's PC.
     * @param cpu The CPU of the User's PC.
     * @param gpu The GPU of the User's PC.
     * @param ram The RAM of the User's PC.
     * @param storage The storage of the User's PC.
     * @param directX The DirectX version of the User's PC.
     */
    public UserPc(
            OperationalSystem os, CPU cpu, GPU gpu, RAM ram,
            Storage storage, DirectX directX) {
        this.os = os;
        this.cpu = cpu;
        this.gpu = gpu;
        this.ram = ram;
        this.storage = storage;
        this.directX = directX;
    }

    /**
     * Gets the OS of the User's PC.
     *
     * @return The OS of the User's PC.
     */
    public OperationalSystem getOs() {
        return os;
    }

    /**
     * Sets the OS of the User's PC.
     *
     * @param os The OS of the User's PC.
     */
    public void setOs(OperationalSystem os) {
        this.os = os;
    }

    /**
     * Gets the CPU of the User's PC.
     *
     * @return The CPU of the User's PC.
     */
    public CPU getCpu() {
        return cpu;
    }

    /**
     * Sets the CPU of the User's PC.
     *
     * @param cpu The CPU of the User's PC.
     */
    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    /**
     * Gets the GPU of the User's PC.
     *
     * @return The GPU of the User's PC.
     */
    public GPU getGpu() {
        return gpu;
    }

    /**
     * Sets the GPU of the User's PC.
     *
     * @param gpu The GPU of the User's PC.
     */
    public void setGpu(GPU gpu) {
        this.gpu = gpu;
    }

    /**
     * Gets the RAM of the User's PC.
     *
     * @return The RAM of the User's PC.
     */
    public RAM getRam() {
        return ram;
    }

    /**
     * Sets the RAM of the User's PC.
     *
     * @param ram The RAM of the User's PC.
     */
    public void setRam(RAM ram) {
        this.ram = ram;
    }

    /**
     * Gets the storage of the User's PC.
     *
     * @return The storage of the User's PC.
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Sets the storage of the User's PC.
     *
     * @param storage The storage of the User's PC.
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     * Gets the DirectX version of the User's PC.
     *
     * @return The DirectX version of the User's PC.
     */
    public DirectX getDirectX() {
        return directX;
    }

    /**
     * Sets the DirectX version of the User's PC.
     *
     * @param directX The DirectX version of the User's PC.
     */
    public void setDirectX(DirectX directX) {
        this.directX = directX;
    }
}
