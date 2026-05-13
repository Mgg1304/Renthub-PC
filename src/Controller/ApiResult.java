package Controller;

public class ApiResult<T> {
	private final boolean ok;
	private final T data;
	private final ApiErrorType errorType;
	private final Integer statusCode;
	private final String userMessage;
	private final String technicalMessage;
	private final Throwable exception;

	private ApiResult(boolean ok, T data, ApiErrorType errorType, Integer statusCode, String userMessage,
			String technicalMessage, Throwable exception) {
		this.ok = ok;
		this.data = data;
		this.errorType = errorType;
		this.statusCode = statusCode;
		this.userMessage = userMessage;
		this.technicalMessage = technicalMessage;
		this.exception = exception;
	}

	public static <T> ApiResult<T> success(T data) {
		return new ApiResult<>(true, data, ApiErrorType.NONE, null, null, null, null);
	}

	public static <T> ApiResult<T> success(T data, Integer statusCode) {
		return new ApiResult<>(true, data, ApiErrorType.NONE, statusCode, null, null, null);
	}

	public static <T> ApiResult<T> error(ApiErrorType errorType, Integer statusCode, String userMessage,
			String technicalMessage, Throwable exception) {
		return new ApiResult<>(false, null, errorType, statusCode, userMessage, technicalMessage, exception);
	}

	public boolean isOk() {
		return ok;
	}

	public T getData() {
		return data;
	}

	public ApiErrorType getErrorType() {
		return errorType;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public String getTechnicalMessage() {
		return technicalMessage;
	}

	public Throwable getException() {
		return exception;
	}
}
