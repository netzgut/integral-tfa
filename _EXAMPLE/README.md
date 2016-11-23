# ∫ Integral Two Factor Authentication Example Usage

We decided to not bundle any UI components with the library (yet) but using it is really easy, in this folder you find some starting points.

## Component

A simple QRCode rendering component is in this folder.

### Dependencies

You need XZing as a dependency for it:

```
compile 'com.google.zxing:javase:3.3.0'
```

### AppModule additions

The component wants an URI and not String, so you should add an TypeCoercer

```
@Contribute(TypeCoercer.class)
public static void supplyTypeCoercer(@SuppressWarnings("rawtypes") Configuration<CoercionTuple> conf) {
    conf.add(new CoercionTuple<>(URI.class, String.class, input -> {
        if (input == null) {
            return null;
        }
        return input.toString();
    }));

    conf.add(new CoercionTuple<>(String.class, URI.class, input -> {
        try {
            return new URI(input);
        }
        catch (URISyntaxException e) {
            return null;
        }
    }));
}
```

### Java

You just have to build the URI and give it to the component

```
...

@Inject
private TwoFactorAuthService authService;

...
public URI getURI() {
    return this.authService.buildURI(this.userSecret, this.issuer);
}
```

### Template

The component renders a QR-Code and includes informal parameters, if it can't render the QR-Code it renders the body of the component as fallback.

```
<t:yourname.QRCodeComponent size="100" uri="uri">
    <b>Couldn't generate valid QRCode</b>
</t:yourname.QRCodeComponent>
```

### Done

See, I told you it's simple
