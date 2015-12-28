package puncher

import (
    "crypto/x509"
    "os/user"
    "path/filepath"
    "os"
    "crypto/tls"
    "fmt"
    "io/ioutil"
)

type Storage struct {
    customDir string
    cert      x509.Certificate
}

func (s Storage) Dir() (string, error) {
    const DefDirName = ".transhift"

    if len(s.customDir) == 0 {
        user, err := user.Current()

        if err != nil {
            return "", err
        }

        return getDir(filepath.Join(user.HomeDir, DefDirName))
    } else {
        return getDir(s.customDir)
    }
}

func (s Storage) Certificate() (tls.Certificate, error) {
    const CertFileName = "puncher_cert.pem"
    const KeyFileName = "puncher_cert.key"
    dir, err := s.Dir()

    if err != nil {
        return tls.Certificate{}, err
    }

    certFilePath := filepath.Join(dir, CertFileName)
    keyFilePath := filepath.Join(dir, KeyFileName)

    if ! fileExists(certFilePath, false) || ! fileExists(keyFilePath, false) {
        fmt.Print("Generating crypto... ")

        keyData, certData, err := createCertificate()

        if err != nil {
            return tls.Certificate{}, err
        }

        err = ioutil.WriteFile(certFilePath, certData, 0600)

        if err != nil {
            return tls.Certificate{}, err
        }

        err = ioutil.WriteFile(keyFilePath, keyData, 0600)

        if err != nil {
            return tls.Certificate{}, err
        }

        fmt.Println("done")
    }

    return tls.LoadX509KeyPair(certFilePath, keyFilePath)
}

func getFile(path string) (*os.File, error) {
    if fileExists(path, false) {
        return os.Open(path)
    }

    return os.Create(path)
}

func getDir(path string) (string, error) {
    if fileExists(path, true) {
        return path, nil
    }

    err := os.MkdirAll(path, 0700)

    if err != nil {
        return "", err
    }

    return path, nil
}

func fileExists(path string, asDir bool) bool {
    info, err := os.Stat(path)

    if err != nil {
        return false
    }

    if asDir {
        return info.IsDir()
    }

    return info.Mode().IsRegular()
}
