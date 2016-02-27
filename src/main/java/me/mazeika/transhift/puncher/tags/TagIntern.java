package me.mazeika.transhift.puncher.tags;

interface TagIntern extends Tag
{
    byte[] intern();

    interface Factory
    {
        TagIntern create(byte[] b);
    }
}
