package me.mazeika.transhift.puncher.tags;

public interface Tag
{
    int LENGTH = 16;

    /**
     * Gets a copy of the backing byte array.
     *
     * @return the backing byte[]
     */
    byte[] get();

    interface Factory
    {
        /**
         * Creates a new {@link Tag} instance.
         *
         * @param b the tag's byte[]
         *
         * @return a new Tag instance
         */
        Tag create(byte[] b);
    }
}
