package me.mazeika.transhift.puncher.tags;

interface TagIntern extends Tag
{
    /**
     * Gets the backing byte array.
     *
     * @return the backing byte[]
     */
    byte[] intern();

    interface Factory
    {
        /**
         * Creates a new {@link TagIntern} instance.
         *
         * @param b the tag's byte[]
         *
         * @return a new TagIntern instance
         */
        TagIntern create(byte[] b);
    }
}
