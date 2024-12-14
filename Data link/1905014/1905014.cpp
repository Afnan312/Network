#include<bits/stdc++.h>
#include<windows.h>
#include<time.h>
using namespace std;

void charToBinary(char ch, vector<int>& val)
{
    int n=(int)ch;
    while (n > 0)
    {
        val.push_back(n%2);
        n=n/2;
    }
    reverse(val.begin(), val.end());
    if(val.size()<8)
    {
        while(val.size()!=8)
        {
            val.push_back(0);
            rotate(val.rbegin(), val.rbegin() + 1, val.rend());
        }
    }
}

char binaryToChar(vector<int>& val)
{
    int ascii=0;
    int dec=0, base=1;
    for(int i=0; i<val.size(); i++)
    {
        if(val[i]==0)
            ascii*=10;
        else if(val[i]==1)
            ascii=ascii*10+1;
    }
    int temp=ascii;
    while(temp){
        int last=temp%10;
        temp/=10;
        dec+=last*base;
        base*=2;
    }
    char ch=(char)dec;
    return ch;
}

vector<int> findXOR(vector<int>& r1, vector<int>& r2)
{
    vector<int> rem;
    bool first=false;
    for(int i=0; i<r1.size(); i++)
    {
        if(r1[i]!=r2[i])
        {
            if(!first)
                first=true;
            rem.push_back(1);
        }
        else{
            if(first)
                rem.push_back(0);
        }
    }
    return rem;
}

vector<int> crc(vector<int>& dividend, vector<int>& generator)
{
    int taken=0;
    int dvdLen=dividend.size();
    int gnrLen=generator.size();
    vector<int> result;
    while(true)
    {
        vector<int> sub;
        sub=result;
        int total=gnrLen-result.size();
        for(int i=0; i<total; i++)
        {
            sub.push_back(dividend[taken]);
            taken++;
        }
        result=findXOR(sub, generator);
        if((taken+gnrLen-result.size())>dvdLen)
            break;
    }
    for(int i=taken; i<dvdLen; i++)
        result.push_back(dividend[taken]);
    return result;
}


int main()
{
    int m;
    string data, gnrStr;
    string space;
    vector<int> gnrPol;
    double probability;
    HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
    cout<<"enter data string: ";
    getline(std::cin, data);
    cout<<"enter number of data bytes in a row: ";
    cin>>m;
    cout<<"enter probability (p): ";
    cin>>probability;
    getline(std::cin, space);
    cout<<"enter generator polynomial: ";
    getline(std::cin, gnrStr);
    for(int i=0; i<gnrStr.size(); i++)//convert string to vector of ints
    {
        if(gnrStr[i]=='0')
            gnrPol.push_back(0);
        else if(gnrStr[i]=='1')
            gnrPol.push_back(1);
    }

    if(data.size()%m!=0)
    {
        int padding=m-(data.size()%m);//find excess bits required for padding
        for(int i=0; i<padding; i++)
            data.push_back('~');
    }

    cout<<endl;
    cout<<endl<<"data string after padding: "<<data<<endl;

    vector<vector<int>> block;//for dividing data into rows of m bytes
    block.resize(data.size()/m);
    int j=0, k=0;
    for(int i=0; i<data.size(); i++)
    {
        vector<int> val;
        charToBinary(data[i], val);//convert to ascii binary
        for(int l=0; l<val.size();l++)
            block[j].push_back(val[l]);
        k++;
        if(k==m)//row is filled, go to next row
        {
            j++;
            k=0;
        }
    }

    int r;
    r=log2(m*8)+1;//no of check bits required

    cout<<endl<<"data block (ascii code of m characters per row):"<<endl;

    for(int i=0; i<block.size(); i++)
    {
        for(j=0; j<block[i].size(); j++)
            cout<<block[i][j];
        cout<<endl;
    }
    cout<<endl;

    int newLength=m*8+r;//original+check bits

    vector<vector<int>> newBlock;//new block with check bits in between

    for(int i=0; i<block.size(); i++)
    {
        int ind=0;
        vector<int> test;
        for(int j=1; j<=newLength; j++)
        {
            if(ceil(log2(j))==floor(log2(j)))
                test.push_back(2);
            else{
                test.push_back(block[i][ind]);
                ind++;
            }
        }
        newBlock.push_back(test);
    }


    cout<<"data block after adding check bits:"<<endl;
    //2 is green, 11 is cyan, 4 is red, 7 for white
    for(int k=0; k<newBlock.size(); k++){
        int count=0;

        for(int i=0; i<newBlock[k].size(); i++)
        {
            if(newBlock[k][i]==2)
            {
                int result=0;
                for(int j=i+1; j<newBlock[k].size(); j++)
                {
                    if(((j+1)&(1<<count))!=0){
                        result^=newBlock[k][j];
                    }
                }
                newBlock[k][i]=result;
                SetConsoleTextAttribute(hConsole, 2);
                count++;
            }
            cout<<newBlock[k][i];
            SetConsoleTextAttribute(hConsole, 7);
        }
        cout<<endl;
    }
    cout<<endl;

    vector<int> serialized;//convert block into column-ordered serialized bits
    for(int i=0; i<newBlock[0].size(); i++)
    {
        for(int j=0; j<newBlock.size(); j++)
        {
            serialized.push_back(newBlock[j][i]);
        }
    }
    cout<<"data bits after column-wise serialization:"<<endl;
    for(int i=0; i<serialized.size(); i++)
        cout<<serialized[i];
    cout<<endl<<endl;

    cout<<"data bits after appending CRC checksum (sent frame):"<<endl;
    vector<int> dividend=serialized;
    int org_size=serialized.size();
    for(int i=0; i<gnrPol.size()-1; i++)//add x-1 0's at the end of dividend
        dividend.push_back(0);
    vector<int> result=crc(dividend, gnrPol);

    int extra=gnrPol.size()-1-result.size();//add the remainder and excess bits at the end of serialized
    for(int i=0; i<extra; i++)
        serialized.push_back(0);
    for(int i=0; i<result.size(); i++)
        serialized.push_back(result[i]);

    for(int i=0; i<serialized.size(); i++)
    {
        if(i>=org_size)
        {
            SetConsoleTextAttribute(hConsole, 11);
        }
        cout<<serialized[i];
    }
    SetConsoleTextAttribute(hConsole, 7);
    cout<<endl<<endl;

    vector<bool> error_vector;//keep track of error bits
    error_vector.resize(serialized.size(), false);

    cout<<"received frame:"<<endl;
    srand(time(0));
    default_random_engine generator(rand());
    uniform_real_distribution<float> distribution(0.0, 1.0);//random float generation

    for(int i=0; i<serialized.size(); i++){
        if(distribution(generator)<probability){
            serialized[i]=1-serialized[i];//toggle
            error_vector[i]=true;
            SetConsoleTextAttribute(hConsole, 4);
        }
        cout<<serialized[i];
        SetConsoleTextAttribute(hConsole, 7);
    }

    cout<<endl<<endl;

    cout<<"result of CRC checksum matching: ";
    vector<int> result2=crc(serialized, gnrPol);
    if(result2.size()==0 || (result2.size()==1 && result2[0]==0))
        cout<<"no error detected"<<endl;
    else
        cout<<"error detected"<<endl;
    cout<<endl;

    for(int i=0; i<(gnrPol.size()-1); i++)//remove the crc checksum bits
    {
        serialized.pop_back();
    }

    vector<vector<int>> deserialize;
    int rows=serialized.size()/newLength;
    deserialize.resize(rows);

    cout<<"data block after removing CRC checksum bits:"<<endl;
    for(int i=0; i<rows; i++)
    {
        int n=0;
        for(int j=0; j<newLength; j++)
        {
            deserialize[i].push_back(serialized[i+n]);
            if(error_vector[i+n])
            {
                SetConsoleTextAttribute(hConsole, 4);
                cout<<deserialize[i][j];
                SetConsoleTextAttribute(hConsole, 7);
            }
            else
                cout<<deserialize[i][j];
            n+=rows;
        }
        cout<<endl;
    }

    for(int k=0; k<deserialize.size(); k++){//check the parity bits again for error detection and correction
        int count=0;
        int pos_count=0;
        for(int i=0; i<deserialize[k].size(); i++)
        {
            if(ceil(log2(i+1))==floor(log2(i+1)))
            {
                int result=0;
                for(int j=i+1; j<deserialize[k].size(); j++)
                {
                    if(((j+1)&(1<<count))!=0){
                        result^=deserialize[k][j];
                    }
                }
                if(deserialize[k][i]!=result)
                {
                    pos_count+=(i+1);
                }
                count++;
            }

        }
        if((pos_count-1)<deserialize[k].size())//if position is within bound
            deserialize[k][pos_count-1]=1-deserialize[k][pos_count-1];
    }

    cout<<endl;

    vector<vector<int>> final_result;
    final_result.resize(rows);

    for(int i=0; i<deserialize.size(); i++)//remove the check bits
    {
        for(int j=0; j<deserialize[i].size(); j++)
        {
            if(ceil(log2(j+1))==floor(log2(j+1)))
                continue;
            final_result[i].push_back(deserialize[i][j]);
        }
    }

    cout<<"data block after removing check bits:"<<endl;
    for(int i=0; i<final_result.size(); i++)
    {
        for(int j=0; j<final_result[i].size(); j++)
            cout<<final_result[i][j];
        cout<<endl;
    }
    cout<<endl;


    cout<<"output frame: ";
    for(int i=0; i<final_result.size(); i++)//convert 8 bit ascii to char
    {
        int m_count=0, words=0;
        while(words<m)
        {
            vector<int> toChar;
            for(int j=m_count; j<m_count+8; j++)
            {
                toChar.push_back(final_result[i][j]);
            }
            char ch=binaryToChar(toChar);
            cout<<ch;
            m_count+=8;
            words++;
        }
    }
    cout<<endl;
    return 0;
}
