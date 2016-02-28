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

    /**
     * Gets if the given array is equal to the backing byte array of this.
     *
     * @param b the byte[] to compare to
     *
     * @return {@code true} if the arrays are equal, {@code false} otherwise
     */
    boolean equalsArray(byte[] b);

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
