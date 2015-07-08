#include <Python/Python.h>
#include "JSolve.h"

/*
 *  Python binding for computing the number of unique solutions to a Sudoku board using 
 *  the JSolve.c solver. Note that JSolve can also return a list of solutions, but this 
 *  is not supported by this binding.
 *
 *  Compile with:
 *     python setup.py build_ext --inplace
 *
 *  See:
 *     https://csl.name/post/c-functions-python/
 *     https://scipy-lectures.github.io/advanced/interfacing_with_c/interfacing_with_c.html
 *     https://docs.python.org/2/c-api/arg.html 
 * 
 *  For example use, see run-tests.py. 
 *
 */

static PyObject* py_JSolve(PyObject* self, PyObject* args) {
  char *input_board;
  int max_solutions;

  if (!PyArg_ParseTuple(args, "si", &input_board, &max_solutions)) {
    /* 
     *  Unable to extract parameters of correct type from argument list. 
     *  JSolve was called with wrong arguments. Say: JSolve(0).
     */ 
    return NULL;
  }
  
  int result = JSolve(input_board, NULL, max_solutions); 
    
  /* Build return value */    
  return Py_BuildValue("i", result);
}

static PyMethodDef py_methods[] = {
  {"JSolve", py_JSolve, METH_VARARGS},
  {NULL, NULL}
};

void initJSolve() {
  (void) Py_InitModule("JSolve", py_methods);
}
