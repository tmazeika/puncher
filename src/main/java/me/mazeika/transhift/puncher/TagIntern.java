package me.mazeika.transhift.puncher;

interface TagIntern extends Tag
{
    byte[] intern();

    interface Factory
    {
        TagIntern create(byte[] b);
    }
}
