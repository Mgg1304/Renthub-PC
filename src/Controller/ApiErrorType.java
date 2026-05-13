package Controller;

public enum ApiErrorType {
	NONE,
	TIMEOUT,
	NETWORK,
	HTTP_4XX,
	HTTP_5XX,
	PARSE,
	VALIDATION,
	UNKNOWN
}
