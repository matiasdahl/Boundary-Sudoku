import itertools
from JSolve import *

def compute_possible_left_sides():
    """
    Return all permutations of [1,2,3,4,5,6,7,8,9] for the left side
    that are compatible with the top row 123456789.

    Since the first row is always 123456789:

     - the first entry in such a permutation must be 1
     - the second entry can not be 1, 2 or 3. This gives 6 possibilities (4,5,6,7,8,9).
     - the third entry can not be 1,2,3 or the second entry. This leaves 5 possibilities. 
     - after the top 3 entries are fixed, this leaves 6! possibilities for the remaining
       entries on the left side.

    In total this gives 6*5*(6!) = 21600 possible configurations for the left side
    (out of 9! = 362880).

    """
    # 362880 entries: 
    perms = list(itertools.permutations(['1','2','3','4','5','6','7','8','9']))

    # fixing top-left entry to 1 leaves 8! = 40320 possible left sides
    fix_1 = [x for x in perms if x[0] == '1']
    
    # Since the top-left 3x3 block can not contain 2 and 3 more
    # than once, we can further prune the possible left sides:
    exclude_23 = [x for x in fix_1 if (x[1] not in ['2', '3'] and x[2] not in ['2', '3'])]
    return exclude_23

def low_left_digit(in_txt): 
    """
    Return the digit in the lower left corner for the board 'in_txt' 
    given as a 81 length string.

    """
    return(in_txt[9*8 + 0])

def insert_left_side(left_side, board_string):
    """
    Replace the left side of the Sudoku board 'board_string' with 'left_side'.
    """
    # inputs should match in upper left corner
    assert(left_side[0] == board_string[0])
    # inputs should match in lower left corner
    assert(left_side[8] == low_left_digit(board_string))

    as_list = list(board_string)
    for idx in range(9):
        as_list[idx*9] = left_side[idx]
    return "".join(as_list)

def top_right_bottom_data():
    """
    Load boards computed in Step 2. Return a list of strings 
    where each string represents one board. 
    """ 
    fhandle = open("top-right-bottom-reduced.txt", "r")
    result = fhandle.read().splitlines()
    fhandle.close()

    # check consistency
    assert(len(result) == 147372)
    for bline in result:
        assert(len(bline) == 81)
        assert(bline[0:9] == "123456789")
    return(result)

def test(board, left_sides):
    """
    For a board 'board' where the top, right, and bottom borders are filled-in, test if 
    its left side can be completed (from the list of left sides 'left_sides') so 
    that there is a unique solution. 

    Returns string:
        <board>, <number of left sides with a unique solution>, 
               , <number of left sides with more than 1 solution>
               , <number of completions compatible with the top and bottom rows>
    """
    assert(len(board) == 81)

    uniques = 0
    non_uniques = 0
    completions = 0

    for left_side in left_sides:
        if low_left_digit(board) == left_side[8]:
            completions += 1
            solutions = JSolve(insert_left_side(left_side, board), 2) 
            assert(solutions == 0 or solutions == 1 or solutions == 2)
            
            if solutions == 1:
                uniques += 1 

            if solutions > 1:
                non_uniques += 1

    return(board + ", " + str(uniques) + ", " + str(non_uniques) + ", " + str(completions))

if __name__ == "__main__":
    left_sides = compute_possible_left_sides() # precompute left sides
    for b in top_right_bottom_data():
        print(test(b, left_sides))

