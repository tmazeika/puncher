package server

import (
	"crypto/rsa"
	"crypto/tls"
	"crypto/x509"
	"encoding/pem"
	"github.com/transhift/appdir"
	"github.com/transhift/common/security"
	"os"
)

func Certificate(a *args) (cert *tls.Certificate, err error) {
	const KeyName  = "pcert.key"
	const CertName = "pcert.pem"
	const FileMode = 0600
	const DefPath = "$HOME/.transhift"
	dir, err := appdir.NewPreferNonEmpty(a.appDir, DefPath)
	if err != nil {
		return
	}

	var privKey rsa.PrivateKey
	// Read or generate private key.
	if err = dir.IfExistsOrOtherwise(KeyName, func(b []byte) (err error) {
		p, _ := pem.Decode(b)
		privKey, err = x509.ParsePKCS1PrivateKey(p)
		return
	}, func(file *os.File) (b []byte, err error) {
		// Set file mode.
		if err = file.Chmod(FileMode); err != nil {
			return
		}
		privKey, b, err = security.GeneratePrivKey()
		return
	}); err != nil {
		return
	}

	// Generate certificate if not exists.
	if err = dir.IfNExists(CertName, func(file *os.File) (b []byte, err error) {
		// Set file mode.
		if err = file.Chmod(FileMode); err != nil {
			return
		}
		return security.CreateCertificate(privKey)
	}); err != nil {
		return
	}

	return tls.LoadX509KeyPair(dir.FilePath(CertName), dir.FilePath(KeyName))
}
