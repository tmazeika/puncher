package puncher

const (
    // UidLength is the length of the UID that the puncher server issues.
    UidLength = 16
)

type ProtocolMessage byte

const (
    DownloadClientType ProtocolMessage = 0x00
    UploadClientType   ProtocolMessage = 0x01
)
