function showNotification(message, type) {
    if (type === "success") {
        iziToast.success({
            title: 'OK',
            message: message
        });
    } else if (type === "error") {
        iziToast.error({
          title: 'Error',
          message: message
        });
    } else if (type === "info") {
        iziToast.info({
          title: 'Hello',
          message: message
        });
    } else if (type === "warning") {
        iziToast.warning({
          title: 'Caution',
          message: message
        });
    } else if (type === "log") {
        iziToast.show({
          title: 'Log',
          message: message,
          color: 'dark',
          progressBarColor: 'rgb(0, 255, 184)'
        });
    } else {

    }
}
